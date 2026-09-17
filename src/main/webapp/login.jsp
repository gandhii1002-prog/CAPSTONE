<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <title>GandhiMart - Login</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            margin: 0;
            padding: 40px;
        }

        .login-box {
            max-width: 400px;
            margin: 60px auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
        }

        input {
            width: 100%;
            box-sizing: border-box;
            padding: 10px;
            margin: 8px 0 16px;
        }

        button {
            padding: 10px 20px;
            cursor: pointer;
        }

        .message {
            margin-top: 20px;
        }

        .error {
            color: red;
        }

        .success {
            color: green;
        }

    </style>

</head>

<body>

<div class="login-box">

    <h1>GandhiMart Login</h1>

    <form id="loginForm">

        <label for="email">
            Email
        </label>

        <input
                type="email"
                id="email"
                required
        >

        <label for="password">
            Password
        </label>

        <input
                type="password"
                id="password"
                required
        >

        <button type="submit">
            Login
        </button>

    </form>

    <div id="message"
         class="message"></div>

    <p>
        <a href="products.jsp">
            Continue to Products
        </a>
    </p>

</div>

<script>

    document
        .getElementById("loginForm")
        .addEventListener("submit", async function(event) {

            event.preventDefault();

            const email =
                document.getElementById("email").value;

            const password =
                document.getElementById("password").value;

            const message =
                document.getElementById("message");

            const formData =
                new URLSearchParams();

            formData.append(
                "email",
                email
            );

            formData.append(
                "password",
                password
            );

            message.className = "message";
            message.textContent = "Logging in...";

            try {

                const response =
                    await fetch(
                        "api/v1/auth/login",
                        {
                            method: "POST",
                            headers: {
                                "Content-Type":
                                    "application/x-www-form-urlencoded"
                            },
                            body: formData
                        }
                    );

                const data =
                    await response.json();

                if (!response.ok) {

                    message.className =
                        "message error";

                    message.textContent =
                        data.error ||
                        "Login failed";

                    return;
                }

                message.className =
                    "message success";

                message.textContent =
                    "Login successful. Redirecting...";

                setTimeout(function() {

                    window.location.href =
                        "products.jsp";

                }, 500);

            } catch (error) {

                message.className =
                    "message error";

                message.textContent =
                    "Unable to connect to server";
            }

        });

</script>

</body>

</html>