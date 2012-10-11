<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<p>Synchronize this repository with <b>${This.name}</b>
<ul>
  <li><a href="${This.uri().toASCIIString()}/documents">documents</a></li>
  <li><a href="${This.uri().toASCIIString()}/vocabularies">vocabularies</li>
  <li><a href="${This.uri().toASCIIString()}/relations">relations</a></li>
</ul></p>

<p>Or change the connection parameters if needed by submitting the form below

<form method="post">
  <input name="location" size="50" type="text" value="${This.location}"/>
  <input type="submit"/>
</form></p>

</@block>
</@extends>
