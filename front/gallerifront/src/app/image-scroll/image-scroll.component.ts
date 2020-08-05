import { Component, OnInit, Input, ViewChild, AfterViewInit, ViewChildren } from '@angular/core';
import { Image, toImageList } from '../../image';
import { HttpService } from '../http.service';
import { CdkVirtualScrollViewport, ScrollDispatcher } from '@angular/cdk/scrolling';
import { PartialObserver } from 'rxjs';

const MIN_ITEM_HEIGHT = 300; // in pixel 
const IMAGE_NOT_LOADED = "../assets/img/img_not_loaded.png";
const THROTTLE_LIMIT_MS = 150; // in ms

@Component({
  selector: 'app-image-scroll',
  templateUrl: './image-scroll.component.html',
  styleUrls: ['./image-scroll.component.css']
})
export class ImageScrollComponent implements OnInit, AfterViewInit {

  itemHeight: number = this.calcItemHeight();
  images: Image[] = [];

  prevTime: number = 0;
  shouldThrottle: boolean = false;

  @ViewChild("virtualScroll", { static: true })
  virtualScroll: CdkVirtualScrollViewport;

  constructor(private http: HttpService, private scrollDispathcer: ScrollDispatcher) { }

  ngAfterViewInit(): void {
    this.virtualScroll.renderedRangeStream.subscribe(this.scrollThrottleObserver());
  }

  /**
   * Observer of CdkVirtualScrollViewport#renderedRangeStream, which decides whether 
   * ImageScrollComponent#get() should throttle image fetching.
   */
  private scrollThrottleObserver(): PartialObserver<any> {
    return {
      next: () => {
        let now = Date.now();
        if (this.prevTime == 0) {
          this.prevTime = now;
          return;
        }

        let diff = now - this.prevTime;
        this.prevTime = now;
        if (diff >= THROTTLE_LIMIT_MS)
          this.shouldThrottle = false;
        else {
          this.shouldThrottle = true;
        }
      }
    };
  }

  ngOnInit() {
    console.log("Item Height:", this.itemHeight);
    this.fetchAllImageUrls();
  }

  fetchAllImageUrls(): void {
    this.http.allImageModels().subscribe({
      next: v => {
        this.images = toImageList(v);
      }
    });
  }

  get(imageUrl: string): string {
    if (this.shouldThrottle)
      return IMAGE_NOT_LOADED;
    else
      return imageUrl;
  }

  private calcItemHeight(): number {
    let height = Math.floor(window.innerHeight * 0.75);
    return height < MIN_ITEM_HEIGHT ? MIN_ITEM_HEIGHT : height;
  }
}
