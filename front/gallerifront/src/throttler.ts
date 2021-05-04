import { PartialObserver } from "rxjs";

const THROTTLE_LIMIT_MS = 150; // in ms

export class Throttler {
  private prevTime: number = 0;
  private throttleFlag: boolean = false;

  /**
   * Observer of CdkVirtualScrollViewport#renderedRangeStream, which decides whether
   * ImageScrollComponent#get() should throttle image fetching.
   */
  scrollThrottleObserver(): PartialObserver<any> {
    return {
      next: () => {
        let now = Date.now();
        if (this.prevTime == 0) {
          this.prevTime = now;
          return;
        }

        let diff = now - this.prevTime;
        this.prevTime = now;
        if (diff >= THROTTLE_LIMIT_MS) this.throttleFlag = false;
        else {
          this.throttleFlag = true;
        }
      },
    };
  }

  shouldThrottle(): boolean {
    return this.throttleFlag;
  }
}
