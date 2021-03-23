# The Alexios Project

v1.0

Copyleft 2021 - Lima Perú.

## What is Alexios?

Alexios is a helper application to generate the TXT files required by SUNAT, the peruvian taxation authority.

The user creates a Google spreadsheet containing the required information, and using Alexios may create the plain text,
pipe (|) separated format.

## What is NOT Alexios?

Alexios is NOT an ERP, nor an accounting software. Neither validates the user inputs, it only transforms data from one
human friendly format to the TXT specified by the Programa de Libros Electrónicos SUNAT (PLE - SUNAT) v5.1.

## Books implementations

The Alexios has interfaces that should be implemented. Those interfaces correspond to each book specified by PLE.

Currently, theese books are implemented:

- LE030000 -> Inventories and balances.
- LE070000 -> Assets.
- LE100000 -> Costs.
