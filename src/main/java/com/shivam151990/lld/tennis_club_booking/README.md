We are designing a Tennis Club booking system. Courts can be booked by players, but we must respect time conflicts and maintenance rules.

We need to solve:

Court Assignment (a)
Assign each booking to a court so that no two overlapping bookings use the same court. Use the minimum number of courts (unlimited available).

Fixed Maintenance Time (b)
After each booking finishes, a court needs X minutes of maintenance before it can be reused.

Durability Maintenance (c)
Each court must undergo an additional maintenance of Y minutes after X uses.

Minimum Courts Only (d)
Instead of assigning bookings to specific courts, only calculate the minimum number of courts needed to handle all bookings.

Conflict Checker (e)
Given two bookings, check if they conflict (overlap in time).