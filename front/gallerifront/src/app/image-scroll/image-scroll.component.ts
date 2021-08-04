import { Component, OnInit, ViewChild, AfterViewInit } from "@angular/core";
import { CdkVirtualScrollViewport } from "@angular/cdk/scrolling";
import { Image, ImageModelList, toImageList } from "../../image";
import { HttpService } from "../http.service";

const MIN_ITEM_HEIGHT = 300; // in pixel
const IMAGE_NOT_LOADED = "../assets/img/img_not_loaded.png";

@Component({
  selector: "app-image-scroll",
  templateUrl: "./image-scroll.component.html",
  styleUrls: ["./image-scroll.component.css"],
})
export class ImageScrollComponent implements OnInit, AfterViewInit {
  readonly LIMIT: number = 5;
  itemHeight: number = this.calcItemHeight();
  images: Image[] = [];
  displayPrevPageBtn: boolean = false;
  displayNextPageBtn: boolean = false;

  @ViewChild("virtualScroll", { static: true })
  virtualScroll: CdkVirtualScrollViewport;

  constructor(private http: HttpService) {}

  ngAfterViewInit(): void {}

  ngOnInit() {
    this.nextPage();
  }

  private calcItemHeight(): number {
    let height = Math.floor(window.innerHeight * 0.75);
    return height < MIN_ITEM_HEIGHT ? MIN_ITEM_HEIGHT : height;
  }

  nextPage(): void {
    let lastId: number =
      this.images.length > 0 ? this.images[this.images.length - 1].id : null;
    this.http.nextPage(lastId, this.LIMIT).subscribe({
      next: (v: ImageModelList) => {
        // append to end
        let np = toImageList(v);
        if (
          this.images.length > 0 &&
          np.length > 0 &&
          np[0].id <= this.images[this.images.length - 1].id
        ) {
          // we alreaady have this page
          return;
        }

        this.images = this.images.concat(np);
        if (this.images.length > 2 * this.LIMIT) {
          this.images.splice(0, this.LIMIT);
        }
        this.scrollToFirst();
      },
    });
  }

  prevPage(): void {
    let lastId: number =
      this.images.length > 0 ? this.images[this.images.length - 1].id : null;
    this.http.prevPage(lastId, this.LIMIT).subscribe({
      next: (v: ImageModelList) => {
        // append to head
        let vp = toImageList(v);
        this.images = vp.concat(this.images);

        if (
          this.images.length > 0 &&
          vp.length > 0 &&
          vp[vp.length - 1].id >= this.images[0].id
        ) {
          // we alreaady have this page
          return;
        }

        if (this.images.length > 2 * this.LIMIT) {
          this.images.splice(this.images.length - this.LIMIT, this.LIMIT);
        }
        this.scrollToFirst();
      },
    });
  }

  scrollToFirst(): void {
    this.virtualScroll.scrollToIndex(0, "auto");
  }
}
