<#include "layout/default.ftl" />

<#macro page_body>
    <h3>Login with Username and Password</h3>
    <form name="spring_security_check_form" action="/j_spring_security_check" method="POST">
        <table>
            <tbody>
            <tr>
                <td>User:</td>
                <td><input type="text" name="j_username" value="" tabindex="1"></td>
            </tr>
            <tr>
                <td>Password:</td>
                <td><input type="password" name="j_password" tabindex="2"></td>
            </tr>
            <tr>
                <td colspan="2"><input name="submit" type="submit" value="Login" tabindex="2"></td>
            </tr>
            </tbody>
        </table>
    </form>
</#macro>

<@page_html/>