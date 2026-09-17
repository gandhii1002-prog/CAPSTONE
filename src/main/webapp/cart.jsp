<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <title>GandhiMart - Cart</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            margin: 0;
            padding: 30px;
            background: #f5f5f5;
        }

        .cart {
            max-width: 900px;
            margin: auto;
        }

        .item {
            background: white;
            padding: 20px;
            margin-bottom: 15px;
            border-radius: 8px;
        }

        .item h3 {
            margin-top: 0;
        }

        input {
            padding: 8px;
            width: 70px;
        }

        button {
            padding: 8px 12px;
            margin-left: 5px;
            cursor: pointer;
        }

        .total {
            background: white;
            padding: 20px;
            margin-top: 20px;
            font-size: 24px;
            font-weight: bold;
            border-radius: 8px;
        }

        .message {
            margin-bottom: 20px;
        }

        .error {
            color: red;
        }

    </style>

</head>

<body>

<div class="cart">

    <h1>My Cart</h1>

    <p>
        <a href="products.jsp">
            Continue Shopping
        </a>
    </p>

    <div id="message" class="message"></div>

    <div id="items"></div>

    <div class="total">

        Total:
        ₹<span id="total">0.00</span>

    </div>

    <br>

    <button onclick="clearCart()">
        Clear Cart
    </button>

    <button onclick="window.location.href='checkout.jsp'">
        Proceed to Checkout
    </button>

</div>

<script>

    async function loadCart() {

        const itemsElement =
            document.getElementById("items");

        const totalElement =
            document.getElementById("total");

        const messageElement =
            document.getElementById("message");

        itemsElement.innerHTML = "";
        messageElement.textContent = "";

        try {

            const response =
                await fetch("api/v1/cart");

            if (response.status === 401) {

                messageElement.textContent =
                    "Please login as a buyer to view your cart.";

                return;
            }

            if (response.status === 403) {

                messageElement.textContent =
                    "Only buyers can use the cart.";

                return;
            }

            if (!response.ok) {
                throw new Error(
                    "Unable to load cart"
                );
            }

            const data =
                await response.json();

            displayCart(data);

            totalElement.textContent =
                Number(data.total).toFixed(2);

        } catch (error) {

            messageElement.className =
                "message error";

            messageElement.textContent =
                error.message;
        }
    }

    function displayCart(data) {

        const container =
            document.getElementById("items");

        container.innerHTML = "";

        if (!data.items ||
                data.items.length === 0) {

            const empty =
                document.createElement("div");

            empty.className = "item";

            empty.textContent =
                "Your cart is empty.";

            container.appendChild(empty);

            return;
        }

        data.items.forEach(item => {

            const card =
                document.createElement("div");

            card.className = "item";

            const name =
                document.createElement("h3");

            name.textContent =
                item.productName;

            const price =
                document.createElement("p");

            price.textContent =
                "Price: ₹" +
                Number(item.productPrice)
                    .toFixed(2);

            const quantity =
                document.createElement("input");

            quantity.type = "number";
            quantity.min = "1";
            quantity.value = item.quantity;

            const updateButton =
                document.createElement("button");

            updateButton.textContent =
                "Update";

            updateButton.onclick =
                () => updateQuantity(
                    item.productId,
                    quantity.value
                );

            const removeButton =
                document.createElement("button");

            removeButton.textContent =
                "Remove";

            removeButton.onclick =
                () => removeItem(
                    item.productId
                );

            const subtotal =
                document.createElement("p");

            subtotal.textContent =
                "Subtotal: ₹" +
                Number(item.subtotal)
                    .toFixed(2);

            card.appendChild(name);
            card.appendChild(price);
            card.appendChild(quantity);
            card.appendChild(updateButton);
            card.appendChild(removeButton);
            card.appendChild(subtotal);

            container.appendChild(card);
        });
    }

    async function updateQuantity(
            productId,
            quantity) {

        const formData =
            new URLSearchParams();

        formData.append(
            "productId",
            productId
        );

        formData.append(
            "quantity",
            quantity
        );

        const response =
            await fetch(
                "api/v1/cart/update",
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

        showMessage(data);

        if (response.ok) {
            loadCart();
        }
    }

    async function removeItem(
            productId) {

        const formData =
            new URLSearchParams();

        formData.append(
            "productId",
            productId
        );

        const response =
            await fetch(
                "api/v1/cart/remove",
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

        showMessage(data);

        if (response.ok) {
            loadCart();
        }
    }

    async function clearCart() {

        const response =
            await fetch(
                "api/v1/cart/clear",
                {
                    method: "POST"
                }
            );

        const data =
            await response.json();

        showMessage(data);

        if (response.ok) {
            loadCart();
        }
    }

    function showMessage(data) {

        const messageElement =
            document.getElementById("message");

        messageElement.textContent =
            data.message ||
            data.error ||
            "";
    }

    loadCart();

</script>

</body>
</html>