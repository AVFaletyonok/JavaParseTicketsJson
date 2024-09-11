# Java Parse tickets.json

The program have the following functionality:
1. Parse json file with next structure:

```json
{
  "tickets": [
    {
      "origin": "VVO",
      "origin_name": "Владивосток",
      "destination": "TLV",
      "destination_name": "Тель-Авив",
      "departure_date": "12.05.18",
      "departure_time": "16:20",
      "arrival_date": "12.05.18",
      "arrival_time": "22:10",
      "carrier": "TK",
      "stops": 3,
      "price": 12400
    },
    ...
  ]
}
```
2. Calculate next statistic characteristic:
- Minimum flight time between Vladivostok and Tel Aviv for each air carrier$
- The difference between the average price and the median for a flight between Vladivostok and Tel Aviv.

3. The program is called from the Windows / Linux command line, and return the results presented in text form.

![program_output.png](materials%2Fprogram_output.png)