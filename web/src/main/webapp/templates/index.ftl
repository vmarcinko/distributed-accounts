<#import "spring.ftl" as spring />

<html>
<body>
    <h1>Distributed Accounts</h1>
    <hr/>

    <#if infoMessage??>
        <span style="color: blue">${infoMessage}</span>
    </#if>
    <#if openedAccountId??>
        <a href="<@spring.url relativeUrl='/accounts/${openedAccountId}' />">Go to</a>
    </#if>

    <h2>Account Opening</h2>
    <form action="<@spring.url relativeUrl='/open'/>" method="post">
        Description: <input type="text" name="description"/>
        <p/>
        <input type="submit" value="Open"/>
    </form>

</body>
</html>