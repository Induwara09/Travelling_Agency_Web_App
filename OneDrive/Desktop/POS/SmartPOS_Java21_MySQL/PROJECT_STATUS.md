# SmartPOS implementation status

## Implemented in this delivery

- Java 21 Spring Boot backend targeting the user's Java 21.0.12 LTS runtime.
- MySQL 8.x local persistence (replacing PostgreSQL from the original specification).
- JWT authentication and backend-enforced Cashier / Manager / Admin roles.
- Local/offline-first POS architecture.
- Cashier menu/cart/checkout UI.
- Cash/card/bank/wallet payment recording.
- Stock deduction and immutable stock movement history.
- Manager/Admin insufficient-stock / negative-stock override.
- Recipe/ingredient consumption engine.
- Products/categories/inventory management.
- Suppliers/purchases and stock receiving.
- Customers.
- Held-order persistence and resume UI.
- Cashier shifts.
- Sales history and controlled Manager/Admin void while retaining original financial records.
- A4 PDF invoice.
- 80mm receipt print UI and KOT local printing path.
- Invoice email queue/retry when connectivity/SMTP is unavailable.
- Sales/inventory dashboard and database-driven reporting.
- CSV/Excel sales exports.
- Manual sales/inventory report email.
- Scheduled monthly sales and inventory report email workflow.
- MySQL backup records and `mysqldump` execution.
- Optional cloud synchronization queue with retry.
- Device heartbeat/health monitoring endpoint and Windows agent script.
- Audit log.
- Windows production build, requirement checker, startup and auto-start scripts.
- Complete original specification document included in the source package.

## Production integration points

The application is a working full-stack POS baseline, but these items depend on the final customer's hardware/provider/business rules and must be finalized during deployment:

- Bank/acquirer terminal API integration and settlement protocol.
- Exact ESC/POS raw-command profile and cash-drawer kick pulse for the selected printer model.
- SMTP provider credentials and sender-domain configuration.
- Real central/cloud API endpoint if multi-branch synchronization is enabled.
- Kitchen printer routing / kitchen-display workflow for the customer's layout.
- Sri Lankan tax/fiscal/business-specific invoice requirements for that customer.
- Remote-monitoring/RMM provider if a third-party RMM product is preferred.

## Recommended next production-hardening items

- Add automated integration/E2E tests on the final Windows + MySQL + printer hardware.
- Add partial-refund UI/engine if the customer requires line-item partial refunds (controlled full void is currently included).
- Add true multi-tender/split-payment capture if required by the business (single selected payment method is implemented).
- Move business/tax/printer configuration from environment variables to a protected database-backed Admin settings editor if non-technical owners need to edit them.
- Add vendor-specific native ESC/POS printing only after selecting the printer model/driver.

Do not deploy to a paying customer before running the acceptance checklist in the README on the actual target hardware.
