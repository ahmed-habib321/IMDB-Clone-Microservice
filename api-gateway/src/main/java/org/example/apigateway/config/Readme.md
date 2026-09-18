# Flow of the Auth Service 

1- Request comes
2- Security Headers Filter : Adds response headers only
3- Rate Limiting Filter : Checks IP / request count
4- JWT Authentication Filter : Extract token → validate → set Authentication
5- Authorization {
    no authentication	401
    wrong role	403
    ok	continue
}
6- Controller execution

