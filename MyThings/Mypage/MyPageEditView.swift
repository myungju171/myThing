//
//  MyPageView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI
import PhotosUI
struct ImagePicker: UIViewControllerRepresentable {
  @Environment(\.presentationMode)
  private var presentationMode
  let sourceType: UIImagePickerController.SourceType
  let onImagePicked: (UIImage) -> Void
  final class Coordinator: NSObject,
                           UINavigationControllerDelegate,
                           UIImagePickerControllerDelegate {
    @Binding
    private var presentationMode: PresentationMode
    private let sourceType: UIImagePickerController.SourceType
    private let onImagePicked: (UIImage) -> Void
    
    init(presentationMode: Binding<PresentationMode>,
         sourceType: UIImagePickerController.SourceType,
         onImagePicked: @escaping (UIImage) -> Void) {
      _presentationMode = presentationMode
      self.sourceType = sourceType
      self.onImagePicked = onImagePicked
    }
    func imagePickerController(_ picker: UIImagePickerController,
                               didFinishPickingMediaWithInfo info: [UIImagePickerController.InfoKey : Any]) {
      let uiImage = info[UIImagePickerController.InfoKey.originalImage] as! UIImage
      onImagePicked(uiImage)
      presentationMode.dismiss()
    }
    func imagePickerControllerDidCancel(_ picker: UIImagePickerController) {
      presentationMode.dismiss()
    }
  }
  func makeCoordinator() -> Coordinator {
    return Coordinator(presentationMode: presentationMode,
                       sourceType: sourceType,
                       onImagePicked: onImagePicked)
  }
  func makeUIViewController(context: UIViewControllerRepresentableContext<ImagePicker>) -> UIImagePickerController {
    let picker = UIImagePickerController()
    picker.sourceType = sourceType
    picker.delegate = context.coordinator
    return picker
  }
  func updateUIViewController(_ uiViewController: UIImagePickerController,
                              context: UIViewControllerRepresentableContext<ImagePicker>) {
  }
}
struct MyPageEditView: View {
  @State var showImagePicker: Bool = false
  @State var isAppear = false
  @State var image = UIImage(named: "deep")
  @State var nameText: String = ""
  @State var messageText: String = ""
  @State var birthDatestr: String = ""
  @State private var birthDate = Date()
  @ObservedObject var viewModel = MypageEditViewModel(network: NetworkService(configuration: .default), userId: 1)
  var model: MyPageModel?
  var body: some View {
    ZStack {
      VStack(spacing: 0) {
        if isAppear == true {
          AsyncImage(url: URL(string: model?.image ?? ""), content: { image in
            image.resizable()
            image.aspectRatio(contentMode: .fit)
          }, placeholder: {Color.gray})
          .frame(width: 200, height: 200)
          .cornerRadius(100)
        } else {
          Image(uiImage: image ?? UIImage())
            .aspectRatio(contentMode: .fit)
            .frame(width: 200, height: 200)
            .cornerRadius(100)
        }
        Button(action: {
          self.showImagePicker.toggle()
        }) {
          Text("사진 편집")
        }
        Text("닉네임")
        TextField("닉네임을 입력해주세요", text: $nameText)
        HStack {
          DatePicker(selection: $birthDate, in: ...Date(), displayedComponents: .date) {
          }.padding()
          Text("생일을 선택해주세요!")
            .foregroundColor(Color(uiColor: .systemGray2))
            .background(Color(uiColor: .secondarySystemBackground))
            .padding()
          //                    DatePicker(selection: $birthDate, in: ...Date(), displayedComponents: .date) {
          //                    }.padding()
        }
        .background(Color(uiColor: .secondarySystemBackground))
        .cornerRadius(10)
        TextField("한 줄 글을 남겨주세요", text: $messageText)
        Text(model?.infoMessage ?? "")
        Spacer()
        Button("수정") {
        viewModel.editMyInfo(image: image ?? UIImage(), userId: 1, name: nameText, infoMessage: messageText, birthDay: birthDate.dateToStringTime()) {
            print("poooost")
          }
        }
        .frame(minWidth: 300, minHeight: 50)
        .foregroundColor(.white)
        .background(Color(.systemBlue))
        .cornerRadius(10)
      }
      .sheet(isPresented: $showImagePicker) {
        ImagePicker(sourceType: .photoLibrary) { image in
          self.image = UIImage(data: image.pngData()!)
          isAppear = false
        }
      }
    }
    .onAppear {
      isAppear = true
      nameText = model?.name ?? "name"
      //      viewModel.search(userId: 1)
    }
  }
}
extension Date {
  func dateToStringTime() -> String {
    let formatter = DateFormatter()
    formatter.locale = Locale(identifier: "ko")
    formatter.dateFormat = "yyyy-MM-dd"
    return formatter.string(from: self)
  }
}
