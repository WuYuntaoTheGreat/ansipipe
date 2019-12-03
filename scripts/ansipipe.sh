#!/dev/null
# ========================================
# DO NOT CALL THIS SCRIPT DIRECTLY
usage() {
    cat <<EOF
Usage:"
<shell> -c ansipipe.sh <shellname> <command>"

Example:
/bin/bash -c ansipipe.sh bash java -jar /path/to/jar
/bin/zsh -c ansipipe.sh zsh java -jar /path/to/jar
EOF
}
# ========================================
echo "===================="
echo $*
echo "===================="
echo $1
echo "===================="

case $1 in
    bash)
        READ_N1="read -s -N1"
        READ_N1_T="read -s -N1 -t 0.001"
        READ_N2_T="read -s -N2 -t 0.001"
        ;;
    zsh)
        READ_N1="read -s -k1"
        READ_N1_T="read -s -k1 -t 0.001"
        READ_N2_T="read -s -k2 -t 0.001"
        ;;
    *)
        echo "$1 not supported!"
        usage
        exit 1
        ;;
esac

shift
if [[ $# -lt 1 ]]; then
    usage
    exit 1
fi

#
# Make FIFO file
#
FIFO_IN=$(mktemp)
rm $FIFO_IN && mkfifo $FIFO_IN

#
# Start internal program
#
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
    $READ_N1    K1
    $READ_N2_T  K2
    $READ_N1_T  K3

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

