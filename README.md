TMC Rest Client
===============

The primary goal of this repository is to provide Terracotta Management Console users with code samples to interact with the TMC Rest Api, or even the Terracotta Agents Rest Api.

You can download the TMC as part as a [Terracotta BigMemory download](http://terracotta.org/downloads)

Scope
-----

The code samples have been tested against TMC 4.3+ and the Terracotta Agents 4.3+

That said, all the examples should work with previous versions (keep in mind though that the /v2 Rest API appeared with 4.2)

The reader of the code samples will adapt the examples to the Rest Client library of his choice.


Official Terracotta Management Console Documentation
----------------------------------------------------

You can find the official TMC Rest Api documentation on the [Terracotta Documentation website](http://terracotta.org/generated/4.3.1/html/bmm-all/#page/bigmemory-max-webhelp%2F_bigmem_max_all.1.497.html%23)


Code Samples
------------

[TMC Rest Api / TSA Rest Agent API Java client code samples](jdk-no-dependencies-code-samples/) : compatible with JDK 6, no dependencies at all

[TMC Rest Api / TSA Rest Agent API CURL code samples](curl.md)

Caveats
-------

The examples are pretty straightforward; but pay attention to :

* 302 Http Status code

1) When you login to the TMC, it's expected to have this status code (check the location redirecting you to /tmc/)

2) Otherwise, it means you did not provide authentication cookies as part of your request (check the location redirecting you to /tmc/login.jsp)


* 403 Http Status code

Be sure to attach to each of your POST, DELETE, PUT requests [the required CsrfTokens](http://terracotta.org/generated/4.3.1/html/bmm-all/#page/bigmemory-max-webhelp%2Fco-uri_security_for_api_without_tmc.html); otherwise you may be forbidden to access the specified resource.
