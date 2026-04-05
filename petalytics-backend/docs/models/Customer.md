```yaml
class:
  name: Customer
  attributes:
    - id: 
        type: UUID
        description: The unique identifier of the customer.
    - username:
        type: String
        description: The username of the customer.
    - marketplace:
        type: Enum
        description: The marketplace where the customer belongs. (e.g., SHOPEE, TIKTOK).
        values:
          - SHOPEE
          - TIKTOK
```