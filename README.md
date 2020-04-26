## TradeRepublic Challenge

### Setting Up

* Building a project with gradle

```
$   ./gradlew clean build --parallel
```

* Run it 

```
./gradlew run
```

### API 

* Instrument-Price retrieval

```
/instruments
```

Example output:

```
{
  "body": [
        {
          "isin": "DN40374C40Q1",
          "description": "vestibulum eos integer salutatus",
          "price": 1646.7927,
          "createdAt": "2020-04-25T17:26:17.090507Z",
          "updatedAt": "2020-04-25T17:27:32.077648Z"
        },
        {
          "isin": "BL3358884058",
          "description": "maiorum reformidans quis sociis expetendis",
          "price": 1142.7518,
          "createdAt": "2020-04-25T17:26:17.031465Z",
          "updatedAt": "2020-04-25T17:27:05.722989Z"
        },
        {
          "isin": "YM37560768Y2",
          "description": "fastidii tamquam nihil vestibulum possit",
          "price": 1094.6853,
          "createdAt": "2020-04-25T17:26:17.073246Z",
          "updatedAt": "2020-04-25T17:27:31.382905Z"
        }
    ],
    "timestamp": "2020-04-25T17:32:09.703543Z"
}
```

* Aggregated-Price History retrieval

```
/candles/{ISIN}
```

Example output:

```
{
  "body": {
    "0": {
      "openTime": "2020-04-25T17:31:17.734108Z",
      "closingTime": "2020-04-25T17:31:55.918283Z",
      "openPrice": 952.6923,
      "closingPrice": 1032.9231,
      "highPrice": 1032.9231,
      "lowPrice": 952.6923
    },
    "1": {
      "openTime": "2020-04-25T17:32:03.021928Z",
      "closingTime": "2020-04-25T17:32:16.552597Z",
      "openPrice": 1020.8462,
      "closingPrice": 1020.6923,
      "highPrice": 1020.8462,
      "lowPrice": 1014.7692
    }
  },
  "timestamp": "2020-04-25T17:32:56.499640Z"
}
```
