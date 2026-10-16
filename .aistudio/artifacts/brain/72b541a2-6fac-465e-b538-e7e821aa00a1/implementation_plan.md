# HostelMate — Smart Expense Splitter for Hostel Students

HostelMate is a modern Android application built with Jetpack Compose that empowers hostel roommates and college students to effortlessly track, split, and settle shared living costs. From daily room groceries and high-stakes weekend trips to complex monthly mess bills with variable attendance days, HostelMate eliminates awkward debt calculations and delivers transparent, fair group finances in Indian Rupees (₹).

***

## User Review & Critical Decisions

> [!IMPORTANT]
> The following choices were confirmed during interactive clarification and form the foundation of this build:

*   **Data & Account Model**: **Local-First with Instant Student Profiles (Room Database)**. The app runs fully offline and persists all transactions locally with rich pre-seeded sample data (Room 204, Goa Trip 2026, Monthly Mess) and a quick profile switcher (switch between Rahul, Priya, Arun, Sneha, Vikram) so any user can instantly test split settlements and notifications from multiple roommate perspectives.
*   **Dashboard Experience**: **Balanced Tri-Pillar Dashboard**. The home view gives balanced prominence to overall personal net balances ("You owe ₹...", "You are owed ₹..."), active Hostel Groups, dedicated Mess Bill tracker, and recent expense activities.
*   **Visual Direction**: **Modern Vibrant Campus Aesthetic**. Deep emerald green (`#0D5C3A` / `#10B981`) primary brand tone paired with warm golden-amber accents (`#F59E0B`), crisp elevation cards, modern typography, and clear status chips (Paid, Pending, Settled).

***

## 1. Overview & Core Concept

Hostel and PG living involves high-frequency, complex shared finances:
1.  **Daily Shared Room Expenses**: Wi-Fi bills, water cans, cleaning supplies, late-night snacks.
2.  **Monthly Mess Bills**: Total catering bills that cannot be split equally because some students went home for festivals, exams, or weekends (requiring absent-day meal deductions).
3.  **Trips & Weekend Getaways**: Multi-payer events where Rahul paid for the hotel, Priya paid for petrol, and Arun paid for dinner.
4.  **Parties & Events**: Birthday celebrations and farewell dinners with uneven split shares.

HostelMate solves this with automated math, simplified debt graphs (minimizing total bank/UPI transfers needed), and clear payment statuses.

***

## 2. User Experience & Visual Design

### Key User Flows

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Bottom Navigation Bar                           │
├───────────────┬────────────────┬────────────────┬──────────────────────┤
│ 1. Dashboard  │ 2. Groups      │ 3. Mess Splitting│ 4. Trip & Events   │
│   • Net Balance│   • Room 204   │   • Total Bill  │   • Goa Trip 2026    │
│   • You Owe   │   • Goa Trip   │   • Absent Days │   • Multi-payer log  │
│   • You're Owed│  • New Group  │   • Per-day rate│   • Settle debts     │
│   • Quick Add │   • Group Code │   • WhatsApp txt│   • Debt Matrix      │
└───────────────┴────────────────┴────────────────┴──────────────────────┘
```

*   **Dashboard**:
    *   **Hero Balance Card**: Displays net financial standing with visual indicators:
        *   Green card if net positive: *"You're owed ₹750"*
        *   Coral red pill if net negative: *"You owe ₹450"*
        *   Highlighting net balance: `+₹300`
    *   **Quick Action FAB & Bar**: "Add Expense", "Split Mess Bill", "Settle Up".
    *   **Category Spending Summary**: Visual breakdown (Food & Groceries, Utilities, Trips, Mess, Party).
    *   **Active Student Profile Chip**: Tap avatar in top bar to switch current persona (e.g., viewing as *Rahul Sharma* vs *Priya Patel*).
*   **Group Management**:
    *   Card grid and list with group type badges (Room, Mess, Trip, Party).
    *   Invite codes (e.g. `HM-ROOM-204`) with quick copy/share affordance.
    *   Roster of members showing each person's individual credit/debit within that group.
*   **Smart Mess Bill Splitting**:
    *   Input total monthly mess bill (e.g., ₹12,000) and billing month (e.g., October 2026).
    *   Split mode selector:
        *   *Equal Split*: Identical share per student.
        *   *Attendance / Days Used Split*: Specify total mess operating days (e.g., 30 days) and individual days attended for each student (e.g., Rahul 30 days, Sneha 18 days due to vacation, Arun 25 days).
    *   Automatic calculation of daily meal rate and per-student share.
    *   Mark status: *Paid* vs *Pending*, with quick "Copy WhatsApp Reminder" text.
*   **Trip & Event Splitter**:
    *   Create trip, record multi-payer expenses (Rahul paid ₹3,000 for stay, Priya paid ₹1,500 for food).
    *   Support custom inclusion (who was part of which meal/cab ride).
    *   **Smart Debt Minimizer**: Automatically converts complex webs of inter-friend debts into minimal transactions (e.g., *"Arun pays Rahul ₹500 to settle all balances"*).

### Visual Identity & Theme Tokens

*   **Primary Brand**: Deep Emerald Green (`#0D5C3A`), Mint Accent (`#10B981`)
*   **Secondary / Accent**: Warm Amber Gold (`#F59E0B`) for action buttons and trip badges
*   **Surface / Background**: Light Pearl Slate (`#F8FAFC`) with elevated pure white cards (`#FFFFFF`) and subtle emerald borders (`#E2E8F0` / `#D1FAE5`)
*   **Financial Semantic Colors**:
    *   Credit / Positive: Forest Green (`#16A34A`)
    *   Debit / Negative: Crimson Coral (`#DC2626`)
    *   Pending Warning: Burnt Orange (`#EA580C`)

***

## 3. Key Product Decisions & Trade-Offs

*   **Decision 1: Local Room Database with Pre-loaded Realistic Sample Data**:
    *   *Chosen Approach*: Pre-populate Room database on first launch with 5 realistic college roommate profiles, active groups (Room 204, Goa Trip, Monthly Mess), and sample transactions.
    *   *Why*: Allows immediate interaction and hands-on testing in the emulator without forcing the user to create 5 dummy accounts or navigate external sign-in flows before seeing calculations in action.
*   **Decision 2: Person-Switcher in the App Bar**:
    *   *Chosen Approach*: A quick switch dialog in the top-right header lets the user toggle between "Rahul (Me)", "Priya", "Arun", and "Sneha".
    *   *Why*: Demonstrates the dual-perspective feature instantly—switching from Rahul (who is owed money) to Arun immediately flips the dashboard to show Arun's pending dues.
*   **Decision 3: Greedy Debt Simplification Algorithm**:
    *   *Chosen Approach*: Standard net-balance greedy matching algorithm.
    *   *Why*: Reduces clutter and avoids cyclical debts (A owes B, B owes C, C owes A) by reducing the group balance to the fewest peer-to-peer payments.

***

## 4. Technical Architecture & Data Strategy

### System Component Diagram

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Jetpack Compose UI Layer                        │
├───────────────────┬───────────────────┬────────────────────────────────┤
│ DashboardScreen   │ GroupsScreen      │ MessSplitterScreen             │
│ TripManagerScreen │ AddExpenseDialog  │ SettleUpDialog                 │
└─────────┬─────────┴─────────┬─────────┴───────────────┬────────────────┘
          │                   │                         │
          ▼                   ▼                         ▼
┌────────────────────────────────────────────────────────────────────────┐
│                    HostelMateViewModel (StateFlow)                     │
│  - Active User State                                                  │
│  - Overall Net Balance & Category Totals                              │
│  - Debt Simplification Engine & Mess Calculation Engine               │
└─────────────────────────────────┬──────────────────────────────────────┘
                                  │
                                  ▼
┌────────────────────────────────────────────────────────────────────────┐
│                        HostelMateRepository                            │
├────────────────────────────────────────────────────────────────────────┤
│ Room Database (AppDatabase):                                           │
│  • StudentDao      • GroupDao        • ExpenseDao    • MessDao         │
└────────────────────────────────────────────────────────────────────────┘
```

### Room Database Schema

1.  **`Student`**: `id`, `name`, `roomNumber`, `email`, `avatarColorHex`, `upiId`
2.  **`ExpenseGroup`**: `id`, `name`, `type` (`ROOM`, `MESS`, `TRIP`, `PARTY`), `description`, `inviteCode`, `createdDate`
3.  **`GroupMember`**: `groupId`, `studentId`, `role` (`ADMIN`, `MEMBER`)
4.  **`Expense`**: `id`, `groupId`, `title`, `amount`, `paidByStudentId`, `category` (`FOOD`, `RENT_UTILITY`, `GROCERY`, `TRIP`, `PARTY`, `OTHER`), `date`, `splitType`
5.  **`ExpenseSplit`**: `id`, `expenseId`, `studentId`, `amount`, `isSettled`
6.  **`MessBill`**: `id`, `groupId`, `monthYear`, `totalAmount`, `totalDays`, `dailyRate`, `notes`
7.  **`MessAttendance`**: `id`, `messBillId`, `studentId`, `daysAttended`, `calculatedAmount`, `isPaid`
8.  **`Settlement`**: `id`, `fromStudentId`, `toStudentId`, `groupId`, `amount`, `settledDate`, `paymentMode`
