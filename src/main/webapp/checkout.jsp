<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <title>GandhiMart - Checkout</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 30px;
            background: #f5f5f5;
        }

        .checkout {
            max-width: 700px;
            margin: auto;
            background: white;
            padding: 30px;
            border-radius: 8px;
        }

        button {
            padding: 12px 20px;
            cursor: pointer;
        }

        .total {
            font-size: 24px;
            font-weight: bold;
            margin: 20px 0;
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

<div class="checkout">

    <h1>Checkout</h1>

    <div id="items"></div>

    <div class="total">
        Total: ₹<span id="total">0.00</span>
    </div>

    <p>
        This project uses a mock payment confirmation.
    </p>

    <button onclick="placeOrder()">
        Confirm Mock Payment & Place Order
    </button>

    <p id="message"></p>

    <p>
        <a href="cart.jsp">
            Back to Cart
        </a>
    </p>

</div>

<script>

    async function loadCart() {

        const response =
            await fetch("api/v1/cart");

        if (!response.ok) {

            document.getElementById("message")
                .textContent =
                "Unable to load cart.";

            return;
        }

        const data =
            await response.json();

        document.getElementById("total")
            .textContent =
            Number(data.total)
                .toFixed(2);

        const items =
            document.getElementById("items");

        items.innerHTML = "";

        if (!data.items ||
                data.items.length === 0) {

            items.textContent =
                "Your cart is empty.";

            return;
        }

        data.items.forEach(item => {

            const div =
                document.createElement("div");

            div.textContent =
                item.productName +
                " × " +
                item.quantity +
                " = ₹" +
                Number(item.subtotal)
                    .toFixed(2);

            items.appendChild(div);
        });
    }

    async function placeOrder() {

        const message =
            document.getElementById("message");

        message.className = "";
        message.textContent =
            "Processing mock payment...";

        const formData =
            new URLSearchParams();

        formData.append(
            "paymentConfirmed",
            "true"
        );

        try {

            const response =
                await fetch(
                    "api/v1/checkout",
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
                    "error";

                message.textContent =
                    data.error ||
                    "Checkout failed";

                return;
            }

            message.className =
                "success";

            message.textContent =
                "Order placed successfully. Order ID: "
                + data.orderId;

        } catch (error) {

            message.className =
                "error";

            message.textContent =
                "Unable to complete checkout.";
        }
    }

    loadCart();

</script>

</body>

</html>