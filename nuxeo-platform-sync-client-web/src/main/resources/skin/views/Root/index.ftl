<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<div>

<p>Synchronize with <a href="${This.path}/${This.location.host};location=${This.location?url('UTF-8')}">${This.location.host}</a></h1> or select from which host 
you want to synchronize by the filling the form below

<form method="POST">
  <input name="host" type="text"/>
  <input type="submit"/>
</form>

</div>

</@block>
</@extends>
