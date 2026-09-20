<%@ page contentType="text/html;charset=UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>GandhiMart - Error</title>

    <style>
        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            margin: 0;
            padding: 50px;
            text-align: center;
        }

        .card {
            max-width: 600px;
            margin: auto;
            background: white;
            padding: 40px;
            border-radius: 10px;
        }

        a {
            text-decoration: none;
        }
    </style>
</head>

<body>

<div class="card">

    <h1>Something went wrong</h1>

    <p>
        GandhiMart could not complete your request.
    </p>

    <p>
        <a href="${pageContext.request.contextPath}/products.jsp">
            Return to GandhiMart
        </a>
    </p>

</div>

</body>
</html>