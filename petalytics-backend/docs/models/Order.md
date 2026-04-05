```yaml
class:
  name: Order
  attributes:
    - id: 
        type: UUID
        description: The unique identifier of the order.
    - customer:
        type: Customer
        description: The customer who placed the order.
    - orderDate:
        type: LocalDateTime
        description: The date and time when the order was placed.
    - completedDate:
        type: LocalDateTime
        description: The date and time when the order was completed.
    - totalAmount:
        type: BigDecimal
        description: The total amount of the order.
    - shippingAddress:
        type: Address
        description: The shipping address for the order.
    - marketplace:
        type: Enum
        description: The marketplace where the order was placed. (e.g., SHOPEE, TIKTOK).
        values:
          - SHOPEE
          - TIKTOK
  relationships:
    - type: ManyToOne
      target: Customer
      joinProperty: customerId
```