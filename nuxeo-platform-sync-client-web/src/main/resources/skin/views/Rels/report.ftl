<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<#if This.dryrun() == 'true'>
<p>That operation will remove all relations from your server and replace them with the
   the listed relations retrieved from <b>${This.location().host}</b>.
  Submit the following form if you want to proceed.
<form method="POST">
  <input type="hidden" name="dryrun" value="false"/>
  <input type="submit"/>
</form>
<#else>
<p>Here is the list of relations restored from <b>${This.location().host}</b></p>
</#if>

<h2>${report.relations?size} relations</h2>
<ol>
<#list report.relations as rel>
 <li>${rel}</li>
</#list>
</ol>

</@block>
</@extends>
