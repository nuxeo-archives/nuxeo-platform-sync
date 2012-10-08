<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<p>Restore <u>vocabularies</u> from <b>${This.location().host}</b> by submitting the form below.</p>

<form method="POST">
  <input name="dryrun" type="hidden" value="true"/> 
  <input type="submit"/>
</form></p>

</@block>
</@extends>
