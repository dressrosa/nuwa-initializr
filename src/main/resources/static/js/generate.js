layui.use(['layer', 'form', 'element'], function() {
    var layer = layui.layer,
        element = layui.element,
        $ = layui.$;
    function queryDependencies() {
        $.ajax({
            url: '../initializr/api/v1/dependencies',
            type: 'post',
            contentType: "application/json;charset=utf-8",
            data: JSON.stringify({
            }),
            error: function(xhr, status, error) {
            },
            success: function(result, status, xhr) {
                if (result.code != '000000') {
                    return
                }

                var nav = document.getElementById('dependencyCollapse')
                for (var key in result.data) {
                    var newElement = document.createElement('div');
                    newElement.className = 'layui-colla-item'
                    var title = document.createElement('div')
                    title.className = "layui-colla-title"
                    title.innerHTML = key
                    var content = document.createElement('div')
                    content.className = "layui-colla-content"
                    for (let value of result.data[key]) {
                        var box = document.createElement('div')
                        box.className = "layadmin-contact-box"
                        box.setAttribute("id", value.id)
                        box.setAttribute("data-selected", "false")
                        if (value.genOption == '1') {
                            box.setAttribute("data-selected", "true")
                        }
                        var html = '<h3>' + value.artifactId + '</h3>'
                            + '<h4>' + value.description + '</h4>'
                        if (value.groupId != '') {
                            html = html + '<p> &lt;dependency></p>'
                                + '<p>&nbsp;&nbsp;&nbsp;&nbsp;&lt;groupId>' + value.groupId + '&lt;/groupId></p>'
                                + '<p>&nbsp;&nbsp;&nbsp;&nbsp;&lt;artifactId>' + value.artifactId + '&lt;/artifactId></p>'
                                + '<p> &lt;/dependency></p>'
                        }
                        box.innerHTML = html
                        content.appendChild(box)
                    }

                    newElement.appendChild(title)
                    newElement.appendChild(content)
                    nav.appendChild(newElement);
                }
                element.render('collapse', 'dependencyPanel');
                initContactBox()
            }
        });
    }
    queryDependencies();

    function initContactBox() {
        /*  点击效果 */
        $(".layadmin-contact-box").each(function() {
            var attr = $(this).attr("data-selected")
            if (attr == 'true') {
                doSelected($(this))
            } else {
                cancelSelected($(this))
            }
            $(this).click(function() {
                var attr = $(this).attr("data-selected")
                if (attr == 'true') {
                    cancelSelected($(this))
                } else {
                    doSelected($(this))
                }
            });
        });
    }

    function doSelected(item) {
        item.attr("data-selected", 'true')
        item.css('border', '2px solid #16baaa')
        item.css('transform', 'scale(0.9)')
    }
    function cancelSelected(item) {
        item.css('border', '');
        item.css('transform', '');
        item.attr("data-selected", 'false')
    }

    var isSubmiting = false
    $("#generateProject").click(() => {
        if (isSubmiting) {
            layer.msg("生成中,请稍后再试");
            return;
        }
        let params = getSubmitParams()
        if (isEmpty(params)) {
            return
        }
        isSubmiting = true
        $.ajax({
            url: '../initializr/api/v1/generate',
            type: 'post',
            contentType: "application/json;charset=utf-8",
            data: getSubmitParams(),
            error: function(xhr, status, error) {
            },
            success: function(res) {
                if (res.code != '000000') {
                    layer.msg(res.message)
                    return
                }
                download(res.data)
            },
            complete: function(XMLHttpRequest, textStatus) {
                isSubmiting = false
            }
        });
    })

    function download(base64Data) {
        var byteArray = base64ToUint8Array(base64Data);
        var blob = new Blob([byteArray], { type: 'application/zip' });
        var url = window.URL.createObjectURL(blob);
        var a = document.createElement('a');
        a.href = url;
        let projectName = document.getElementById('projectName').value
        a.download = projectName + '.zip';

        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }
    function base64ToUint8Array(base64) {
        const raw = atob(base64);
        const len = raw.length;
        const arr = new Uint8Array(len);
        for (let i = 0; i < len; i++) {
            arr[i] = raw.charCodeAt(i);
        }

        return arr;
    }

    function getSubmitParams() {
        let projectName = document.getElementById('projectName').value
        if (isEmpty(projectName)) {
            layer.msg('项目名称不能为空');
            return "";
        }
        let packageName = document.getElementById('packageName').value
        if (isEmpty(packageName)) {
            layer.msg('基础包名不能为空');
            return ""
        }
        let groupId = document.getElementById('groupId').value
        if (isEmpty(groupId)) {
            layer.msg('GroupId不能为空');
            return ""
        }
        let artifactId = document.getElementById('artifactId').value
        if (isEmpty(artifactId)) {
            layer.msg('ArtifactId不能为空');
            return ""
        }
        let depens = document.querySelectorAll("div[data-selected='true']")
        var depenIds = [];
        if (depens != null) {
            for (let item of depens) {
                depenIds.push(item.id);
            }
        }
        var structName = document.querySelector('input[name="structName"]:checked').value
        let description = document.getElementById('description').value


        return JSON.stringify({
            projectName: projectName,
            packageName: packageName,
            groupId: groupId,
            artifactId: artifactId,
            description: description,
            structName: structName,
            dependencyIds: depenIds
        })
    }




});