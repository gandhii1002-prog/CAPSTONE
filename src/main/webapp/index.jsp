<%@ page contentType="text/html;charset=UTF-8" %>

<!DOCTYPE html>
<html lang="en">

<head>

    <meta charset="UTF-8">

    <meta
            name="viewport"
            content="width=device-width, initial-scale=1.0">

    <title>GandhiMart - Online Marketplace</title>

    <style>

        * {
            box-sizing: border-box;
        }

        body {
            margin: 0;
            font-family: Arial, sans-serif;
            background: #f5f5f5;
            color: #222;
        }

        header {
            background: #222;
            color: white;
            padding: 18px 30px;
        }

        nav {
            max-width: 1100px;
            margin: auto;
            display: flex;
            align-items: center;
            justify-content: space-between;
            gap: 20px;
        }

        .logo {
            font-size: 25px;
            font-weight: bold;
        }

        nav a {
            color: white;
            text-decoration: none;
            margin-left: 18px;
        }

        .hero {
            max-width: 1100px;
            margin: 50px auto;
            padding: 60px 30px;
            text-align: center;
            background: white;
            border-radius: 12px;
        }

        .hero h1 {
            font-size: 44px;
            margin-bottom: 15px;
        }

        .hero p {
            font-size: 19px;
            color: #555;
            max-width: 700px;
            margin: 0 auto 30px;
            line-height: 1.6;
        }

        .buttons {
            display: flex;
            justify-content: center;
            gap: 15px;
            flex-wrap: wrap;
        }

        .button {
            display: inline-block;
            padding: 13px 24px;
            border-radius: 6px;
            text-decoration: none;
            font-weight: bold;
            border: 1px solid #222;
        }

        .primary {
            background: #222;
            color: white;
        }

        .secondary {
            background: white;
            color: #222;
        }

        .features {
            max-width: 1100px;
            margin: 30px auto 60px;
            padding: 0 20px;
            display: grid;
            grid-template-columns:
                repeat(auto-fit, minmax(220px, 1fr));
            gap: 20px;
        }

        .feature {
            background: white;
            padding: 25px;
            border-radius: 10px;
        }

        .feature h3 {
            margin-top: 0;
        }

        .feature p {
            color: #666;
            line-height: 1.5;
        }

        footer {
            text-align: center;
            padding: 25px;
            background: #222;
            color: white;
        }

    </style>

</head>

<body>

<header>

    <nav>

        <div class="logo">
            GandhiMart
        </div>

        <div>

            <a href="<%= request.getContextPath() %>/products.jsp">
                Products
            </a>

            <a href="<%= request.getContextPath() %>/cart.jsp">
                Cart
            </a>

            <a href="<%= request.getContextPath() %>/orders.jsp">
                My Orders
            </a>

            <a href="<%= request.getContextPath() %>/login.jsp">
                Login
            </a>

        </div>

    </nav>

</header>

<section class="hero">

    <h1>
        Welcome to GandhiMart
    </h1>

    <p>
        A multi-seller online marketplace where buyers
        can discover products, manage their cart,
        place orders, and review their purchases.
    </p>

    <div class="buttons">

        <a
                class="button primary"
                href="<%= request.getContextPath() %>/products.jsp">

            Browse Products

        </a>

        <a
                class="button secondary"
                href="<%= request.getContextPath() %>/login.jsp">

            Login / Register

        </a>

    </div>

</section>

<section class="features">

    <div class="feature">

        <h3>
            🛒 Shopping Cart
        </h3>

        <p>
            Add products, update quantities,
            remove items, and view your running total.
        </p>

    </div>

    <div class="feature">

        <h3>
            💳 Easy Checkout
        </h3>

        <p>
            Place orders using the GandhiMart
            mock payment confirmation system.
        </p>

    </div>

    <div class="feature">

        <h3>
            ⭐ Product Reviews
        </h3>

        <p>
            Rate purchased products from
            one to five stars and leave feedback.
        </p>

    </div>

    <div class="feature">

        <h3>
            👥 Multi-Seller Marketplace
        </h3>

        <p>
            Sellers can manage listings while
            buyers browse products from the marketplace.
        </p>

    </div>

</section>

<footer>

    GandhiMart &copy; 2026

</footer>

</body>

</html>
