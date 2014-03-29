<#import "spring.ftl" as spring />

<html>
<body>
    <h1>Distributed Accounts</h1>
    <hr/>

    <#if infoMessage??>
        <table style="background-color: #54e6ff" cellpadding="5">
            <tr>
                <td>${infoMessage}</td>
            </tr>
        </table>
    </#if>

    <br/>

    <form action="<@spring.url relativeUrl='/index'/>" method="get">
        Search by description: <input type="text" name="descriptionQueryValue"/>
        <input type="submit" value="Search"/>
    </form>

    <ul>
    <#list accounts as account>
            <li>
                <strong>ID:</strong> <a href="<@spring.url relativeUrl='/accounts/${account.id}'/>">${account.id}</a>,
                <strong>Description:</strong> ${account.description}
            </li>
    </#list>
    </ul>

    <h2>Account Opening</h2>
    <form action="<@spring.url relativeUrl='/open'/>" method="post">
        Description: <input type="text" name="description"/>
        <p/>
        <input type="submit" value="Open"/>
    </form>

</body>
</html>