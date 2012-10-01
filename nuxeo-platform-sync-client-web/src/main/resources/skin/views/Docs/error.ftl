<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<p>Something goes wrong while synchronizing documents from <a href="${This.location().toString()}">${This.location().host}</a>. Here is the error message :</br>

${error.message}
</p>
</@block>
</@extends>
