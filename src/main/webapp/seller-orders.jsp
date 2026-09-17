<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <title>GandhiMart - Incoming Orders</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 30px;
            background: #f5f5f5;
        }

        .container {
            max-width: 1000px;
            margin: auto;
        }

        .order {
            background: white;
            padding: 20px;
            margin-bottom: 15px;
            border-radius: 8px;
        }

        .error {
            color: red;
        }

    </style>

</head>

<body>

<div class="container">

    <h1>Incoming Orders</h1>

    <p>
        <a href="products.jsp">
            Products
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
                            "api/v1/orders/seller"
                    );

            const data =
                    await response.json();

            if (!response.ok) {

                message.className = "error";

                message.textContent =
                        data.error ||
                        "Unable to load incoming orders";

                return;
            }

            if (!data.orders ||
                    data.orders.length === 0) {

                container.textContent =
                        "No incoming orders yet.";

                return;
            }

            data.orders.forEach(item => {

                const card =
                        document.createElement("div");

                card.className = "order";

                const title =
                        document.createElement("h3");

                title.textContent =
                        "Order #" +
                        item.orderId;

                const buyer =
                        document.createElement("p");

                buyer.textContent =
                        "Buyer: " +
                        item.buyerName;

                const product =
                        document.createElement("p");

                product.textContent =
                        "Product: " +
                        item.productName;

                const quantity =
                        document.createElement("p");

                quantity.textContent =
                        "Quantity: " +
                        item.quantity;

                const subtotal =
                        document.createElement("p");

                subtotal.textContent =
                        "Your product subtotal: ₹" +
                        Number(
                                item.subtotal
                        ).toFixed(2);

                const total =
                        document.createElement("p");

                total.textContent =
                        "Order total: ₹" +
                        Number(
                                item.orderTotal
                        ).toFixed(2);

                const status =
                        document.createElement("p");

                status.textContent =
                        "Order status: " +
                        item.status;

                const payment =
                        document.createElement("p");

                payment.textContent =
                        "Payment status: " +
                        item.paymentStatus;

                const date =
                        document.createElement("p");

                date.textContent =
                        "Placed: " +
                        item.createdAt;

                card.appendChild(title);
                card.appendChild(buyer);
                card.appendChild(product);
                card.appendChild(quantity);
                card.appendChild(subtotal);
                card.appendChild(total);
                card.appendChild(status);
                card.appendChild(payment);
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