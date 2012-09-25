<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<div style="margin: 5px 5px 5px 5px">
Restore documents from a remote server
</div>

<div style="margin: 10px 10px 10px 10px">
<form method="post">
  server URL : <input name="server url" type="text" value="${details.url}"/>
  query name : <input name="queryName" type="text"/>
</form>
</div>

</@block>
</@extends>
