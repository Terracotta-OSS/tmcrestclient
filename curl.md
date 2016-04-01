Curl examples for the TMC Rest Api
==================================

Login against the TMC
-----------------
    $ curl -c myTmcCookieJar.txt 'http://localhost:9889/tmc/login.jsp'  -H 'Content-Type: application/x-www-form-urlencoded'  --data 'username=admin&password=admin'

The interesting part here is the "-c myTmcCookieJar.txt", that will create a file to store the cookies in it.

Get the list of Cache Managers
------------------------------

non authenticated, against an agent :

    $ curl http://localhost:9540/tc-management-api/v2/agents/cacheManagers

authenticated, against the TMC :

    $ curl -b myTmcCookieJar.txt http://localhost:9889/tmc/api/v2/agents/cacheManagers

The interesting part here is the "-b myTmcCookieJar.txt", that will read the cookies previously saved in a file.

Also interesting for what's to come, is displaying the headers of the response :


    $ curl -D -  -b myTmcCookieJar.txt  http://localhost:9889/tmc/api/v2/agents/

    HTTP/1.1 200 OK
    Date: Fri, 01 Apr 2016 02:45:28 GMT
    Cache-Control: no-cache
    Expires: Thu, 01 Jan 1970 00:00:00 GMT
    Pragma: no-cache
    OWASP_CSRFTOKEN: EEDF-A7OX-750Q-6KM1-LZ6W-V4AJ-OBSD-L0I3
    Content-Length: 172
    Content-Type: application/json
    Server: Jetty(8.1.15.v20140411)

    {"agentId":"TMS","apiVersion":"v2","entities":[{"agentId":"LocalCluster","agencyOf":"TSA","rootRepresentables":{"urls":"http://192.168.1.21:9540"}}],"exceptionEntities":[]}


Trigger a backup
-------------

non authenticated, against an agent :

    $ curl http://localhost:9540/tc-management-api/v2/agents;ids=embedded/backups;name=MyBackup

authenticated, against the TMC :

    $ curl -X POST  -H "OWASP_CSRFTOKEN: EEDF-A7OX-750Q-6KM1-LZ6W-V4AJ-OBSD-L0I3" -H "X-Requested-With: OWASP CSRFGuard Project" -b myTmcCookieJar.txt 'http://localhost:9889/tmc/api/v2/agents;ids=LocalCluster/backups;name=MyBackup'

    {"agentId":"TMS","apiVersion":"v2","entities":[{"agentId":"LocalCluster","sourceId":"OnlyServer","name":"backup.20160331.225335","status":"RUNNING","error":null}],"exceptionEntities":[]}

The interesting part here is the use of the CSRF token; using 2 extra headers, with the -H flags

Also, please note how the agent id is "embedded" for a TSA Rest Agent, whereas, from the TMC, it's the connection name. ("LocalCluster here")

Trigger a distributed GC (DGC)
------------------------------

non authenticated, against an agent :

    $ curl http://localhost:9540/tc-management-api/v2/agents;ids=embedded/diagnostics/dgc

authenticated, against the TMC :

    $ curl -i -X POST  -H "OWASP_CSRFTOKEN: EEDF-A7OX-750Q-6KM1-LZ6W-V4AJ-OBSD-L0I3" -H "X-Requested-With: OWASP CSRFGuard Project" -b myTmcCookieJar.txt 'http://localhost:9889/tmc/api/v2/agents;ids=LocalCluster/diagnostics/dgc'

    HTTP/1.1 200 OK
    Date: Fri, 01 Apr 2016 02:59:32 GMT
    Cache-Control: no-cache
    Expires: Thu, 01 Jan 1970 00:00:00 GMT
    Pragma: no-cache
    OWASP_CSRFTOKEN: FZU5-JNJA-11Y6-701S-HF0J-ULXP-FG44-0LW9
    Content-Length: 4
    Content-Type: application/json
    Server: Jetty(8.1.15.v20140411)

Bypass SSL checks
-----------------

It's never a good idea to do that in production, so keep that option for testing only :


    $ curl -k -c myTmcCookieJar.txt 'http://localhost:9889/tmc/login.jsp'  -H 'Content-Type: application/x-www-form-urlencoded'  --data 'username=admin&password=admin'

The interesting part here is the -k option, for insecure.