# Backend for Pixel Painter

## API consists from groups:
1. account
1. gallery
1. likes

### Account consists from methods:
#### 1. login
##### Input(GET): 
1. login : string (required) (min length = 6; max length = 32; consists only chars a..z + A..Z + 0..9)
1. password(non-hashed) : string (required) (min length = 6; max length = 128)
##### Output: 
1. status(OK, FAIL) : string 
1. token : string (if OK return string length = 32 else empty string)
##### Example:
```address_server/account/login?login=some_login&password=some_password```
#### 2. register
##### Input(GET): 
1. login : string (required) (min length = 6; max length = 32; consists only chars a..z + A..Z + 0..9)
1. email : string (required) (max length = 64)
1. password(non-hashed) : string (required) (min length = 6; max length = 128)
##### Output:
1. status(OK, FAIL) : string
##### Example:
```address_server/account/register?login=some_login&password=some_password```
#### 3. edit
##### Input(GET):
1. field : string (required) (takes one of the following values: password, email, first_name, second_name, age, vk_profile, country)
1. value : string (required)
1. token : string (required) (length = 32)
##### Output:
1. status(OK, FAIL, INVALID_TOKEN) : string
##### Example:
```address_server/account/edit?field=email&value=test@test.ru&token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx```

### Gallery consists from methods:
#### 1. get
##### Input(GET):
1. offset : integer
1. count : integer
1. like_order : bool
1. token : string (length = 32)
##### Output:
1. status(OK, FAIL) : string
1. items : array of Art

Art = {art_id : integer, data : string, owner: bool}
owner = (owner picture == owner token)
##### Examples:
```address_server/gallery/get```

```address_server/gallery/get?token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx```

```address_server/gallery/get?offset=0&count=2&token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx```
#### 2. create
##### Input(GET):
1. data : string (required)
1. is_private : bool (required)
1. token : string (required) (length = 32)
##### Output:
1. status(OK, FAIL, INVALID_TOKEN) : string
1. art_id : integer
##### Example: 
```address_server/gallery/create?data=data:image/png;base64,...&is_private=1&token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx```
#### 3. edit
##### Input(GET):
1. art_id : integer
1. data : string (required)
1. token : string (required) (length = 32)
##### Output:
1. status(OK, FAIL, INVALID_TOKEN) : string
##### Example:
```address_server/gallery/edit?art_id=123&data=data:image/png;base64,...&token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx```

### Likes consists from methods:
#### 1. add
##### Input(GET):
1. art_id : integer (required)
1. token : string (required)
##### Output:
1. status(OK, FAIL, INVALID_TOKEN) : string
##### Example:
```address_server/likes/add?art_id=123&token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx```
#### 1. remove
##### Input(GET):
1. art_id : integer (required)
1. token : string (required) (length = 32)
##### Output:
1. status(OK, FAIL, INVALID_TOKEN) : string
##### Example:
```address_server/likes/remove?art_id=123&token=xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx```
