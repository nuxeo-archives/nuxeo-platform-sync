<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<h2>Internal Server Error</h2>

<p>${error.message}</p>
<pre>${stack}</pre>

</@block>
</@extends>
