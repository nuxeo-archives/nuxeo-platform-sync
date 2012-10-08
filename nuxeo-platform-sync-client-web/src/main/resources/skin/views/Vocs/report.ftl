<@extends src="base.ftl">
<@block name="header">You signed in as ${Context.principal}</@block>

<@block name="content">

<#if This.dryrun() == 'true'>
<p>That operation will remove all vocabularies from your server and replace them with the following
   list from <b>${This.location().host}</b>.
  Submit the form if you want to proceed.
<form method="POST">
  <input type="hidden" name="dryrun" value="false"/>
  <input type="submit"/>
</form>
<#else>
<p>Here is the list of vocabularies restored from <b>${This.location().host}</b></p>
</#if>

<h2>${report.vocabularies?size} vocabularies</h2>
<ol>
<#list report.vocabularies as voc>
 <li>${voc}</li>
</#list>
</ol>

</@block>
</@extends>
