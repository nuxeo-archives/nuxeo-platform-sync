<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<#if This.host().dryrun >
<p>Here is the list of changes from <b>${This.host().name}</b>. Submit the following form if you want to proceed ?
<form method="POST" action="${action}">
  <input type="hidden" name="query" value="${This.query}"/>
  <input type="submit"/>
</form>
<#else>
<p>Here is the list of changes processed from <b>${This.host().name}</b></p>
</#if>

<h2>${report.added?size} added documents</h2>
<ol>
<#list report.added as path>
 <li>${path.toString()}</li>
</#list>
</ol>

<h2>${report.removed?size} removed documents</h2>
<ol>
<#list report.removed as uid>
 <li>${uid}</li>
</#list>
</ol>

<h2>${report.moved?size} moved documents</h2>
<ol>
<#list report.moved as path>
 <li>${path}</li>
</#list>
</ol>

<h2>${report.updated?size} updated documents</h2>
<ol>
<#list report.updated as path>
 <li>${path}</li>
</#list>
</ol>

</@block>
</@extends>
