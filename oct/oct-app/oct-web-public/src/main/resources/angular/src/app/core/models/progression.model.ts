export class Progression {
    constructor(public goal: number, public signatureCount: number) {}

    getPercent(): number {
        let percent = 0;

        if (this.signatureCount && this.goal) {
            percent = Math.round(this.signatureCount / this.goal * 100);
        }

        if (percent > 100) {
            percent = 100;
        }

        return percent;
    }
}
