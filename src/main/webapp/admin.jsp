<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <title>GandhiMart - Admin Panel</title>

    <style>

        body {
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            margin: 0;
            padding: 30px;
        }

        .container {
            max-width: 1200px;
            margin: auto;
        }

        section {
            background: white;
            padding: 20px;
            margin-bottom: 25px;
            border-radius: 8px;
        }

        .item {
            border-bottom: 1px solid #ddd;
            padding: 15px 0;
        }

        .item:last-child {
            border-bottom: none;
        }

        button {
            padding: 8px 14px;
            cursor: pointer;
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

<div class="container">

    <h1>GandhiMart Admin Panel</h1>

    <p>
        <a href="products.jsp">
            Products
        </a>
    </p>

    <div id="message"></div>

    <section>

        <h2>All Users</h2>

        <div id="users">
            Loading...
        </div>

    </section>

    <section>

        <h2>All Orders</h2>

        <div id="orders">
            Loading...
        </div>

    </section>

    <section>

        <h2>All Product Listings</h2>

        <div id="products">
            Loading...
        </div>

    </section>

</div>

<script>

    async function loadUsers() {

        const response =
                await fetch(
                        "api/v1/admin/users"
                );

        const data =
                await response.json();

        if (!response.ok) {
            throw new Error(
                    data.error ||
                    "Unable to load users"
            );
        }

        const container =
                document.getElementById(
                        "users"
                );

        container.innerHTML = "";

        if (data.users.length === 0) {

            container.textContent =
                    "No users found.";

            return;
        }

        data.users.forEach(user => {

            const item =
                    document.createElement(
                            "div"
                    );

            item.className = "item";

            item.textContent =
                    "#" + user.id +
                    " | " +
                    user.name +
                    " | " +
                    user.email +
                    " | Role: " +
                    user.role +
                    " | Created: " +
                    user.createdAt;

            container.appendChild(item);
        });
    }

    async function loadOrders() {

        const response =
                await fetch(
                        "api/v1/admin/orders"
                );

        const data =
                await response.json();

        if (!response.ok) {
            throw new Error(
                    data.error ||
                    "Unable to load orders"
            );
        }

        const container =
                document.getElementById(
                        "orders"
                );

        container.innerHTML = "";

        if (data.orders.length === 0) {

            container.textContent =
                    "No orders found.";

            return;
        }

        data.orders.forEach(order => {

            const item =
                    document.createElement(
                            "div"
                    );

            item.className = "item";

            item.textContent =
                    "Order #" +
                    order.orderId +
                    " | Buyer: " +
                    order.buyerName +
                    " | Status: " +
                    order.status +
                    " | Payment: " +
                    order.paymentStatus +
                    " | Total: ₹" +
                    Number(
                            order.totalAmount
                    ).toFixed(2);

            container.appendChild(item);
        });
    }

    async function loadProducts() {

        const response =
                await fetch(
                        "api/v1/admin/products"
                );

        const data =
                await response.json();

        if (!response.ok) {
            throw new Error(
                    data.error ||
                    "Unable to load products"
            );
        }

        const container =
                document.getElementById(
                        "products"
                );

        container.innerHTML = "";

        if (data.products.length === 0) {

            container.textContent =
                    "No product listings found.";

            return;
        }

        data.products.forEach(product => {

            const item =
                    document.createElement(
                            "div"
                    );

            item.className = "item";

            const text =
                    document.createElement(
                            "span"
                    );

            text.textContent =
                    "#" +
                    product.id +
                    " | " +
                    product.name +
                    " | Seller ID: " +
                    product.sellerId +
                    " | ₹" +
                    Number(
                            product.price
                    ).toFixed(2) +
                    " | Stock: " +
                    product.stock;

            const button =
                    document.createElement(
                            "button"
                    );

            button.textContent =
                    "Remove Listing";

            button.onclick =
                    () => removeProduct(
                            product.id
                    );

            item.appendChild(text);
            item.appendChild(
                    document.createTextNode(" ")
            );
            item.appendChild(button);

            container.appendChild(item);
        });
    }

    async function removeProduct(
            productId) {

        const confirmed =
                confirm(
                        "Remove this product listing?"
                );

        if (!confirmed) {
            return;
        }

        const formData =
                new URLSearchParams();

        formData.append(
                "productId",
                productId
        );

        const response =
                await fetch(
                        "api/v1/admin/products/delete",
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

        const message =
                document.getElementById(
                        "message"
                );

        if (!response.ok) {

            message.className = "error";

            message.textContent =
                    data.error ||
                    "Unable to remove product";

            return;
        }

        message.className = "success";

        message.textContent =
                data.message;

        loadProducts();
    }

    async function loadAdminPanel() {

        try {

            await loadUsers();
            await loadOrders();
            await loadProducts();

        } catch (error) {

            const message =
                    document.getElementById(
                            "message"
                    );

            message.className = "error";

            message.textContent =
                    error.message;
        }
    }

    loadAdminPanel();

</script>

</body>

</html>