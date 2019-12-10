# ========================================
# DO NOT CALL THIS SCRIPT DIRECTLY
usage() {
    cat <<EOF
Make sure to set following variables before sourcing "ansipipe.sh"
\$SHNAME  - (bash|zsh)
\$COMMAND -

EOF
}
# ========================================

case $SHNAME in
    bash)
        READ_N=N
        ;;
    zsh)
        READ_N=k
        ;;
    *)
        usage
        exit 1
        ;;
esac

if [[ $COMMAND == "" ]]; then
    usage
    exit 1
fi

# 
# Read raw input from keyboard. 
# 
read_raw_keys () {
    local K1 K2 K3 key

    # Trap the alarm char input.
    # This will cause error.
    trap "" SIGALRM

    read -s -r -${READ_N}1 -t 2 K1
    [[ -z $K1 ]] && return
    read -s -r -${READ_N}2 -t 0.001 K2
    read -s -r -${READ_N}1 -t 0.001 K3

    key="$K1$K2$K3"


    if [[ $key =~ $'\n' ]]; then
        key="<CR>"
    fi

    case "$key" in
        " ")
            key="<SP>"
            ;;
        #'\')
        #    key="<BSL>"
        #    ;;
        #\t)
        #    key="<TAB>"
        #    ;;
    esac

    # Release trap.
    trap - SIGALRM

    # Send to pipe
    echo "$key"
}

#
# Main loop
#
main () {
    trap 'echo "<size $(stty size)>"' SIGWINCH
    echo "<size $(stty size)>"
    echo "<CR>"
    while :; do
        read_raw_keys
    done
}

main | $COMMAND

