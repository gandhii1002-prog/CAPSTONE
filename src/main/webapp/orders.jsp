<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <title>GandhiMart - My Orders</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 30px;
            background: #f5f5f5;
        }

        .container {
            max-width: 900px;
            margin: auto;
        }

        .order {
            background: white;
            padding: 20px;
            margin-bottom: 15px;
            border-radius: 8px;
        }

        .status {
            font-weight: bold;
        }

        .total {
            font-size: 20px;
            font-weight: bold;
        }

        .error {
            color: red;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>My Orders</h1>

    <p>
        <a href="products.jsp">
            Continue Shopping
        </a>
        |
        <a href="cart.jsp">
            My Cart
        </a>
    </p>

    <div id="message"></div>

    <div id="orders"></div>

</div>

<script>

    async function loadOrders() {

        const message =
                document.getElementById("message");

        const container =
                document.getElementById("orders");

        message.textContent = "";
        container.innerHTML = "";

        try {

            const response =
                    await fetch(
                            "api/v1/orders/my"
                    );

            const data =
                    await response.json();

            if (!response.ok) {

                message.className = "error";

                message.textContent =
                        data.error ||
                        "Unable to load orders";

                return;
            }

            if (!data.orders ||
                    data.orders.length === 0) {

                container.textContent =
                        "You have no orders yet.";

                return;
            }

            data.orders.forEach(order => {

                const card =
                        document.createElement("div");

                card.className = "order";

                const title =
                        document.createElement("h3");

                title.textContent =
                        "Order #" +
                        order.id;

                const status =
                        document.createElement("p");

                status.className = "status";

                status.textContent =
                        "Status: " +
                        order.status;

                const total =
                        document.createElement("p");

                total.className = "total";

                total.textContent =
                        "Total: ₹" +
                        Number(
                                order.totalAmount
                        ).toFixed(2);

                const date =
                        document.createElement("p");

                date.textContent =
                        "Placed: " +
                        order.createdAt;

                card.appendChild(title);
                card.appendChild(status);
                card.appendChild(total);
                card.appendChild(date);

                container.appendChild(card);
            });

        } catch (error) {

            message.className = "error";

            message.textContent =
                    "Unable to connect to server.";
        }
    }

    loadOrders();

</script>

</body>

</html>