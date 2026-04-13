# Petalytics API Documentation

## Order Ingestion Controller

```js
host: /api/ingestion
```

### Upload

```js
POST {{host}}/upload

// Request Parameter
- file: MultipartFile
- marketplace: String

// Response
200 OK
```

---