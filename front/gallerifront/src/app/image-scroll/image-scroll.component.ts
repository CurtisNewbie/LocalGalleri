import { Component, OnInit, ViewChild, AfterViewInit } from "@angular/core";
import { CdkVirtualScrollViewport } from "@angular/cdk/scrolling";
import { Image, ImageModelList, toImageList } from "../../image";
import { HttpService } from "../http.service";

const ITEM_HEIGHT = 300; // in pixel
const IMAGE_NOT_LOADED = "../assets/img/img_not_loaded.png";

@Component({
  selector: "app-image-scroll",
  templateUrl: "./image-scroll.component.html",
  styleUrls: ["./image-scroll.component.css"],
})
export class ImageScrollComponent implements OnInit, AfterViewInit {
  readonly LIMIT: number = 5;
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

  nextPage(): void {
    let lastId: number =
      this.images.length > 0 ? this.images[this.images.length - 1].id : null;
    this.http.nextPage(lastId, this.LIMIT).subscribe({
      next: (v: ImageModelList) => {
        // append to end
        let np = toImageList(v);
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
