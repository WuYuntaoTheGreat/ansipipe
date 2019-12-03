#!/usr/local/bin/bash

if [[ $# -lt 1 ]]; then
    echo "Usage: $0 <command>"
    exit 1
fi

PREFIX=ansipipe
#FIFO_IN=/tmp/$PREFIX.in.fifo
FIFO_IN=$(mktemp)

#[ -p $FIFO_IN  ] || mkfifo $FIFO_IN
rm $FIFO_IN && mkfifo $FIFO_IN

$* < $FIFO_IN &
INNER_PID=$!

#
# Trap function to kill inner program when Ctrl-C pressed.
#
on_interrupt () {
    echo "existing..."
    echo "killing internal program $INNER_PID ..."
    kill -9 $INNER_PID 2>/dev/null
    rm $FIFO_IN
    trap "" SIGINT
    exit 0
}
trap on_interrupt SIGINT

# 
# Read raw input from keyboard. 
# 
read_raw_keys () {
    local K1 K2 K3 key

    # Trap the alarm char input.
    # This will cause error.
    trap "" SIGALRM
    read -s -N1 K1
    read -s -N2 -t 0.001 K2
    read -s -N1 -t 0.001 K3

    key="$K1$K2$K3"

    if [[ $key =~ $'\n' ]]; then
        # Convert carriage return
        key="<CR>"
    fi

    # Release alarm trap.
    trap - SIGALRM

    # Send to pipe
    echo "$(stty size):$key" > $FIFO_IN
}

#####################
# Main loop
#####################
while :; do
    read_raw_keys
done

