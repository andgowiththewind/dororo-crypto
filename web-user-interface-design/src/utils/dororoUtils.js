export function devConsoleLog(...data) {
    if (process.env.NODE_ENV === 'development') {
        console.log(...data);
    }
}
