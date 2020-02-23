package com.lzt.boke.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.lzt.boke.dto.NotificationDTO;
import com.lzt.boke.dto.PageInfoDTO;
import com.lzt.boke.enums.NotificationStatusEnum;
import com.lzt.boke.enums.NotificationTypeEnum;
import com.lzt.boke.exception.CustomizeErrorCode;
import com.lzt.boke.exception.CustomizeException;
import com.lzt.boke.mapper.NotificationMapper;
import com.lzt.boke.model.Notification;
import com.lzt.boke.model.NotificationExample;
import com.lzt.boke.model.User;
import com.sun.org.apache.bcel.internal.generic.NEW;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class NotificationService {
    @Autowired
    private NotificationMapper notificationMapper;

    public PageInfoDTO<NotificationDTO> list(long userId, Integer pageNum, Integer pageSize) {
        PageInfoDTO<NotificationDTO> notificationDTOPageInfo = new PageInfoDTO<>();
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId);
        if (pageNum == null || pageNum < 1) {
            pageNum = 1;
        } else if (pageNum > this.getPages(pageSize, notificationExample)) {
            pageNum = this.getPages(pageSize, notificationExample);
        }

        //使用PageHelper进行分页查询
        notificationExample.setOrderByClause("gmt_create desc");
        PageHelper.startPage(pageNum, pageSize);
        List<Notification> notifications = notificationMapper.selectByExample(notificationExample);

        PageInfo<Notification> pageInfo = new PageInfo<>(notifications, 7);
        pageInfo.setNavigateLastPage(this.getPages(pageSize, notificationExample));
        pageInfo.setNavigateFirstPage(1);
        notificationDTOPageInfo.setPageInfo(pageInfo);

        if (notifications.size() == 0) {
            return notificationDTOPageInfo;
        }


        List<NotificationDTO> notificationDTOS = new ArrayList<>();
        for (Notification notification : notifications) {
            NotificationDTO notificationDTO = new NotificationDTO();
            BeanUtils.copyProperties(notification, notificationDTO);
            notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
            notificationDTOS.add(notificationDTO);
        }
        notificationDTOPageInfo.setDataCollection(notificationDTOS);
        return notificationDTOPageInfo;
    }

    public Integer getPages(int pageSize, NotificationExample notificationExample) {
        int totalRecords = (int) notificationMapper.countByExample(notificationExample);
        int totalPages = totalRecords % pageSize == 0 ? totalRecords / pageSize : (totalRecords / pageSize + 1);
        return totalPages;
    }

    /**
     * 统计未阅读通知
     * @param userId
     * @return
     */
    public Long unreadCount(Long userId) {
        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria()
                .andReceiverEqualTo(userId)
                .andStatusEqualTo(NotificationStatusEnum.UNREAD.getStatus());
        return notificationMapper.countByExample(notificationExample);
    }


    /**
     * 获取通知内容并修改通知当状态
     *
     * @param id
     * @param user
     * @return
     */
    public NotificationDTO read(Long id, User user) {
        Notification notification = notificationMapper.selectByPrimaryKey(id);
        if (notification == null) {
            throw new CustomizeException(CustomizeErrorCode.NOTIFICATION_NOT_FOUND);
        }
        if (!Objects.equals(notification.getReceiver(), user.getId())) {
            throw new CustomizeException(CustomizeErrorCode.READ_NOTIFICATION_FAIL);
        }

        notification.setStatus(NotificationStatusEnum.READ.getStatus());
        notificationMapper.updateByPrimaryKey(notification);

        NotificationDTO notificationDTO = new NotificationDTO();
        BeanUtils.copyProperties(notification, notificationDTO);
        notificationDTO.setTypeName(NotificationTypeEnum.nameOfType(notification.getType()));
        return notificationDTO;

    }
}
