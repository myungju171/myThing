//
//  SearchView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/20.
//
import SwiftUI
import Combine

struct SearchView: View {
  @State var searchQuery = ""
  @StateObject private var viewModel = SearchViewModel(network: NetworkService(configuration: .default))
  var body: some View {
    NavigationView{
      VStack {
        List(viewModel.items, id: \.title) { item in
          NavigationLink {
            SearchItemDetailView(model: item)
          } label: {
            HStack {
              AsyncImage(url: URL(string: item.image), content: { image in
                image.resizable()
              }, placeholder: {
                Image("deep")
                  .aspectRatio(contentMode: .fit)
                  .frame(width: 100, height: 100)
              })
              .aspectRatio(contentMode: .fit)
              .frame(width: 100, height: 100)
              .cornerRadius(10)
              VStack(alignment: .leading, spacing: 10) {
                Text(item.title.replacingOccurrences(of: "<b>", with: "").replacingOccurrences(of: "</b>", with: ""))
                  .fontWeight(.bold)
                HStack(spacing: 0) {
                  Text(item.lprice.decimalWon())
                }
              }
              .padding()
            }
          }
          .listRowSeparator(.hidden)
        }
      }
    }
    .searchable(text: $searchQuery)
    .onSubmit(of: .search) {
      viewModel.search(searchText: searchQuery)
    }
  }
}
struct ContentViewone_Previews: PreviewProvider {
  static var previews: some View {
    SearchView()
  }
}
