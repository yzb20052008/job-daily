<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8"/>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0"/>
    <title>个人简历</title>
    <style type="text/css">
        @page{
            size: 210mm 297mm; /*设置纸张大小:A4(210mm 297mm)、A3(297mm 420mm) 横向则反过来*/
            margin: 0.25in;
            padding: 1em;
        }
        /*表格整体样式*/
        table{
            border:1px solid #333;
            /*width: 210mm;*/
            /* 让表格位于网页中间 */
            margin: auto;
            /* 合并表格与单元格之间的边框，单元格与单元格之间的边框 */
            border-collapse: collapse;
        }
        /*给标题文字设置大小*/
        caption{
            font-size: 30px;
        }
        /*单元格样式*/
        td{
            border: 1px solid #333;;
            /*单元格中文字到单元格边框的距离*/
            padding: 15px 5px;
            text-align: center;
        }
    </style>
</head>
<body  style="font-family: 'FangSong_GB2312'">
&nbsp;
<table>
    <caption style="margin-bottom: 10px;font-weight: bold">个人简历</caption>
    <tbody>
    <tr>
        <td width="10%">姓名</td>
        <td width="12%">${resume.name}</td>
        <td width="10%">性别</td>
        <td width="12%">${resume.sex}</td>
        <td width="10%">出生年月</td>
        <td width="12%">${resume.birthday}</td>
        <td width="12%" rowspan="3"><img style="width: 70px;height: 110px" src="${resume.avatar}"/></td>
    </tr>
    <tr>
        <td>联系电话</td>
        <td>${resume.phone}</td>
        <td>学历</td>
        <td>${resume.education}</td>
        <td>工作年限</td>
        <td>${resume.workYear}</td>
    </tr>
    <tr>
        <td>毕业时间</td>
        <td>${resume.timeToWork}</td>
        <td>邮箱</td>
        <td colspan="3">${resume.email}</td>
    </tr>

    <tr>
        <td colspan="">个人技能</td>
        <td colspan="6" style="text-align: left;padding: 15px">${resume.personalSkill}</td>
    </tr>

    <tr>
        <td colspan="">求职期望</td>
        <td colspan="6" style="text-align: left;padding: 15px">${resume.jobExpectList}</td>
    </tr>
    <tr>
        <td colspan="">工作经历</td>
        <td colspan="6" style="text-align: left;padding: 15px">${resume.workExpList}</td>
    </tr>
    <tr>
        <td colspan="">项目经历</td>
        <td colspan="6" style="text-align: left;padding: 15px">${resume.proExpList}</td>
    </tr>
    <tr>
        <td colspan="">教育经历</td>
        <td colspan="6" style="text-align: left;padding: 15px">${resume.eduExpList}</td>
    </tr>
    </tbody>
</table>
</body>
</html>
