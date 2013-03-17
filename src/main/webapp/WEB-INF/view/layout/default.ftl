<#include "settings.ftl" />
<#import "../macro/spring.ftl" as spring />

<#macro page_html>
    <#escape x as x?html>
        <#if isAjax?? && isAjax>
            <@page_body/>
        <#else>
            <!DOCTYPE html>
            <html lang="en_GB">
                <head>
                    <@page_head/>
                </head>
                <#flush>
                <body onunload="">
                    <@page_body/>
                    <@page_js/>
                </body>
            </html>
        </#if>
    </#escape>
</#macro>

<#macro page_head>
<@page_meta/>
<title>some silly title to have on this page></title>
<@page_css/>
<link rel="shortcut icon" href="/resources/icon/favicon.ico" />
<link rel="apple-touch-icon" href="/resources/icon/icon-57.png"/>
</#macro>

<#macro page_meta>
<meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=yes">
<meta name="format-detection" content="telephone=no">
<meta name="description" content="some silly description to have on this page">
<meta name="apple-mobile-web-app-status-bar-style" content="black" />
</#macro>

<#macro page_css>

</#macro>

<#macro page_body>

</#macro>

<#macro page_js>

</#macro>
