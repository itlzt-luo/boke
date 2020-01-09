/**
 * 提交回复
 */


// $("#commentButton").onclick(post);
function post() {
    alert("发起回复");
    var questionId = $("#question_id").val();
    var content = $("#comment_content").val();
    this.comment2target(questionId, 1, content);
}

function comment2target(targetId, type, content) {
    alert("回复");
    if (!content) {
        alert("不能回复空内容");
        return;
    }

    $.ajax({
        type: "POST",
        url: "/comment",
        contentType: 'application/json',
        data: JSON.stringify({
            "parentId": targetId,
            "content": content,
            "type": type
        }),
        success: function (response) {
            if (response.code == 200) {
                window.location.reload();
            } else {
                if (response.code == 2003) {/*未登录状态*/
                    var isAccepted = confirm(response.message);
                    if (isAccepted) {
                        window.open("https://github.com/login/oauth/authorize?client_id=9114c7123540490a24bf&redirect_uri=" + document.location.origin + "/callback&scope=user&state=1");
                        window.localStorage.setItem("closable", true);
                    } else {
                        alert(response.message);
                    }
                }
            }
        },
        dataType: "json"

    });
}
