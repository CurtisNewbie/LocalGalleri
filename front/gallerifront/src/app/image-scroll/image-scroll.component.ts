import { Component, OnInit, ViewChild, AfterViewInit } from "@angular/core";
import { CdkVirtualScrollViewport } from "@angular/cdk/scrolling";
import { Image, toImageList } from "../../image";
import { HttpService } from "../http.service";

import { Throttler } from "src/throttler";

const MIN_ITEM_HEIGHT = 300; // in pixel
const IMAGE_NOT_LOADED = "../assets/img/img_not_loaded.png";

@Component({
  selector: "app-image-scroll",
  templateUrl: "./image-scroll.component.html",
  styleUrls: ["./image-scroll.component.css"],
})
export class ImageScrollComponent implements OnInit, AfterViewInit {
  readonly LIMIT: number = 10;
  itemHeight: number = this.calcItemHeight();
  images: Image[] = [];
  currPage: number = 0;
  throttler: Throttler = new Throttler();
  private isThrottlerEnabled: boolean = true;

  @ViewChild("virtualScroll", { static: true })
  virtualScroll: CdkVirtualScrollViewport;

  constructor(private http: HttpService) {}

  ngAfterViewInit(): void {
    this.virtualScroll.renderedRangeStream.subscribe(
      this.throttler.scrollThrottleObserver()
    );
  }

  ngOnInit() {
    // use pagination instead of fetching all images
    //   console.log("Item Height:", this.itemHeight);
    //   this.fetchAllImageUrls();
    this.nextPage();
  }

  private fetchAllImageUrls(): void {
    this.http.allImageModels().subscribe({
      next: (v) => {
        this.images = toImageList(v);
      },
    });
  }

  private fetchImageUrlsByPage(limit: number, page: number): void {
    this.http.imageModelsOf(page, limit).subscribe({
      next: (v) => {
        this.images = toImageList(v);
        this.currPage = page;
      },
    });
  }

  get(imageUrl: string): string {
    if (this.throttler.shouldThrottle() && this.isThrottlerEnabled) {
      return IMAGE_NOT_LOADED;
    }
    return imageUrl;
  }

  private calcItemHeight(): number {
    let height = Math.floor(window.innerHeight * 0.75);
    return height < MIN_ITEM_HEIGHT ? MIN_ITEM_HEIGHT : height;
  }

  nextPage(): void {
    this.fetchImageUrlsByPage(this.LIMIT, this.currPage + 1);
  }

  lastPage(): void {
    if (this.currPage == 1) {
      return;
    }
    this.fetchImageUrlsByPage(this.LIMIT, this.currPage - 1);
  }
}
