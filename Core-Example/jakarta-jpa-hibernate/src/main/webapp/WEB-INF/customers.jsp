<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="jakarta.tags.core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <title>مدیریت مشتریان</title>
    <style>
        body { font-family: Arial; margin: 20px; direction: rtl; }
        table { border-collapse: collapse; width: 100%; margin-top: 20px; }
        th, td { border: 1px solid #ddd; padding: 8px; text-align: right; }
        th { background-color: #4CAF50; color: white; }
        .form-group { margin: 10px 0; }
        label { display: inline-block; width: 100px; }
        input[type="text"], input[type="email"] { width: 250px; padding: 5px; }
        button { padding: 5px 15px; margin: 5px; cursor: pointer; }
        .delete-btn { color: red; text-decoration: none; }
        .edit-btn { color: blue; text-decoration: none; margin-left: 10px; }
    </style>
</head>
<body>
<h1>🏦 مدیریت مشتریان بانک</h1>

<h3>افزودن مشتری جدید</h3>
<form method="post" action="${pageContext.request.contextPath}/customers">
    <div class="form-group">
        <label>نام کامل:</label>
        <input type="text" name="fullName" required>
    </div>
    <div class="form-group">
        <label>ایمیل:</label>
        <input type="email" name="email">
    </div>
    <div class="form-group">
        <label>تلفن:</label>
        <input type="text" name="phone">
    </div>
    <button type="submit">افزودن مشتری</button>
</form>

<h3>لیست مشتریان</h3>
<table>
    <thead>
    <tr>
        <th>شناسه</th>
        <th>نام کامل</th>
        <th>ایمیل</th>
        <th>تلفن</th>
        <th>عملیات</th>
    </tr>
    </thead>
    <tbody>
    <c:forEach items="${customers}" var="customer">
        <tr>
            <td>${customer.id}</td>
            <td>${customer.fullName}</td>
            <td>${customer.email}</td>
            <td>${customer.phone}</td>
            <td>
                <a href="${pageContext.request.contextPath}/customers?action=edit&id=${customer.id}" class="edit-btn">ویرایش</a>
                <a href="${pageContext.request.contextPath}/customers?action=delete&id=${customer.id}"
                   onclick="return confirm('آیا از حذف این مشتری مطمئن هستید؟')"
                   class="delete-btn">حذف</a>
            </td>
        </tr>
    </c:forEach>
    </tbody>
</table>
</body>
</html>