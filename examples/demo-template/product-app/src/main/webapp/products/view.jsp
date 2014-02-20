<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
 pageEncoding="ISO-8859-1"%>
<%@ page import="org.keycloak.example.oauth.ProductDatabaseClient" %>
<%@ page import="org.keycloak.util.KeycloakUriBuilder" %>
<html>
<head>
    <title>Product View Page</title>
</head>
<body bgcolor="#F5F6CE">
<%
   String logoutUri = KeycloakUriBuilder.fromUri("http://localhost:8080/auth/rest/realms/demo/tokens/logout")
                                     .queryParam("redirect_uri", "http://localhost:8080/product-portal").build().toString();
    String acctUri =   "http://localhost:8080/auth/rest/realms/demo/account";
%>

<p>Goto: <a href="http://localhost:8080/customer-portal">customers</a> | <a href="<%=logoutUri%>">logout</a> | <a href="<%=acctUri%>">manage acct</a></p>
User <b><%=request.getUserPrincipal().getName()%></b> made this request.
<h2>Product Listing</h2>
<%
    java.util.List<String> list = null;
    try {
        list = ProductDatabaseClient.getProducts(request);
    } catch (ProductDatabaseClient.Failure failure) {
        out.println("There was a failure processing request.  You either didn't configure Keycloak properly, or maybe" +
                "you just forgot to secure the database service?");
        out.println("Status from database service invocation was: " + failure.getStatus());
        return;

    }
    for (String cust : list)
{
   out.print("<p>");
   out.print(cust);
   out.println("</p>");

}
%>
<br><br>
</body>
</html>