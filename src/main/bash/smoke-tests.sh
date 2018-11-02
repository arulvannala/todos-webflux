#!/bin/bash

http :8010/ title="make bacon pancakes"
http :8010/ title="make vegan bacon pancakes"
http :8010/ title="eat pancakes"

http PATCH :8010/1 completed=true
http PATCH :8010/2 completed=true

