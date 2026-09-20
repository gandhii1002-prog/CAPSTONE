<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta name="viewport"
          content="width=device-width, initial-scale=1.0">

    <title>GandhiMart - Register</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 0;
            background: #f5f5f5;
        }

        .container {
            max-width: 450px;
            margin: 60px auto;
            background: white;
            padding: 30px;
            border-radius: 10px;
            box-shadow: 0 2px 10px rgba(0,0,0,0.08);
        }

        h1 {
            text-align: center;
            margin-bottom: 25px;
        }

        label {
            display: block;
            margin-top: 15px;
            margin-bottom: 6px;
            font-weight: bold;
        }

        input,
        select {
            width: 100%;
            padding: 11px;
            box-sizing: border-box;
            border: 1px solid #ccc;
            border-radius: 5px;
        }

        button {
            width: 100%;
            margin-top: 20px;
            padding: 12px;
            background: #222;
            color: white;
            border: none;
            border-radius: 5px;
            cursor: pointer;
        }

        button:disabled {
            opacity: 0.6;
            cursor: not-allowed;
        }

        .message {
            margin-top: 15px;
        }

        .error {
            color: #b00020;
        }

        .success {
            color: green;
        }

        .links {
            text-align: center;
            margin-top: 20px;
        }

        .links a {
            text-decoration: none;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>Create Account</h1>

    <form id="registerForm">

        <label for="name">
            Name
        </label>

        <input
                type="text"
                id="name"
                required
                maxlength="100"
                placeholder="Enter your name">

        <label for="email">
            Email
        </label>

        <input
                type="email"
                id="email"
                required
                maxlength="255"
                placeholder="Enter your email">

        <label for="password">
            Password
        </label>

        <input
                type="password"
                id="password"
                required
                minlength="6"
                placeholder="Minimum 6 characters">

        <label for="role">
            Account Type
        </label>

        <select id="role">

            <option value="BUYER">
                Buyer
            </option>

            <option value="SELLER">
                Seller
            </option>

        </select>

        <button
                type="submit"
                id="registerButton">

            Register

        </button>

    </form>

    <div
            id="message"
            class="message">
    </div>

    <div class="links">

        Already have an account?

        <a href="login.jsp">
            Login
        </a>

    </div>

</div>

<script>

    document
        .getElementById("registerForm")
        .addEventListener(
            "submit",
            async function(event) {

                event.preventDefault();

                const button =
                    document.getElementById(
                        "registerButton"
                    );

                const message =
                    document.getElementById(
                        "message"
                    );

                message.className =
                    "message";

                message.textContent =
                    "";

                const formData =
                    new URLSearchParams();

                formData.append(
                    "name",
                    document.getElementById(
                        "name"
                    ).value.trim()
                );

                formData.append(
                    "email",
                    document.getElementById(
                        "email"
                    ).value.trim()
                );

                formData.append(
                    "password",
                    document.getElementById(
                        "password"
                    ).value
                );

                formData.append(
                    "role",
                    document.getElementById(
                        "role"
                    ).value
                );

                button.disabled = true;

                try {

                    const response =
                        await fetch(
                            "<%= request.getContextPath() %>/api/v1/auth/register",
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
                            "Registration failed.";

                        button.disabled = false;

                        return;
                    }

                    message.className =
                        "message success";

                    message.textContent =
                        data.message ||
                        "Registration successful.";

                    document
                        .getElementById(
                            "registerForm"
                        )
                        .reset();

                } catch (error) {

                    message.className =
                        "message error";

                    message.textContent =
                        "Unable to connect to GandhiMart.";

                } finally {

                    button.disabled = false;
                }
            }
        );

</script>

</body>

</html>
