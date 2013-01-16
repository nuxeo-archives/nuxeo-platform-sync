<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<p>Restore <u>documents</u> from <b>${This.host().name}</b> by submitting the form below.

<form method="POST">
  <dl>
    <dd>query</dd><dt><input name="query" type="text" value="${This.query}"/></dt>
  <input type="submit"/>
</form></p>

</@block>
</@extends>
