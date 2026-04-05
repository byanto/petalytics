```yaml
class:
  name: OrderItem
  attributes:
    - id: 
        type: UUID
        description: The unique identifier of the order item.
    - order:
        type: Order
        description: The order the item belongs to.
    - productSKU:
        type: String
        description: The sku of the product.
    - productName:
        type: String
        description: The name of the product.
    - productPrice:
        type: BigDecimal
        description: The price of the product.
    - quantity:
        type: Integer
        description: The quantity of the product.
  relationships:
    - type: ManyToOne # An OrderItem belongs to one Order
      target: Order
      joinProperty: orderId
```