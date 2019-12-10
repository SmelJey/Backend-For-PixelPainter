# Backend for Pixel Painter

## API consists from groups:
1. account
1. gallery

### Account consists from methods:
#### 1. login
##### Input(GET): 
1. login : string (required) (max length = 32)
1. password(non-hashed) : string (required) (min length = 6)
##### Output: 
1. status(OK, FAIL) : string 
1. token : string (if OK return string length = 32 else empty string)
##### Example:
```address_server/account/login?login=some_login&password=some_password```
#### 2. register
##### Input(GET): 
1. login : string (required) (max length = 32)
1. password(non-hashed) : string (required) (min length = 6)
##### Output:
1. status(OK, FAIL) : string
##### Example:
```address_server/account/register?login=some_login&password=some_password```